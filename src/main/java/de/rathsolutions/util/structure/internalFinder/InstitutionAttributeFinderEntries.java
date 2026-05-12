/*-
 * #%L
 * SchoolfinderBackend
 * %%
 * Copyright (C) 2020 - 2021 Rathsolutions. <info@rathsolutions.de>
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package de.rathsolutions.util.structure.internalFinder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.rathsolutions.jpa.entity.School;
import de.rathsolutions.jpa.repo.SchoolRepo;
import de.rathsolutions.jpa.service.SchoolDAOService;
import de.rathsolutions.util.finder.pojo.FinderEntity;
import de.rathsolutions.util.finder.pojo.FinderEntitySearchConstraint;
import de.rathsolutions.util.structure.AbstractEntries;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope("singleton")
@Slf4j
public class InstitutionAttributeFinderEntries extends AbstractEntries {

	private static final long serialVersionUID = 8979518050945610433L;

	@Autowired
	private SchoolRepo schoolRepo;

	@Autowired
	private SchoolDAOService schoolDaoService;

	private Map<Long, List<FinderEntity>> finderEntityForProjectsMap = new HashMap<>();

	private Instant lastCachedUpdate;

	private ReentrantReadWriteLock entryListLock = new ReentrantReadWriteLock();

	/**
	 * Builds the internal entry list with all searchable information from
	 * institutions
	 */
	private void buildEntryList() {
		var writeLock = entryListLock.writeLock();
		writeLock.lock();
		try {
			log.info("Updating entry list");
			this.clear();
			List<School> allEntities = schoolRepo.findAll();

			allEntities.stream().forEach(e -> {
				List<String> additionalSearchableInformation = schoolDaoService.getAdditionalSearchableInformation(e);
				additionalSearchableInformation.addAll(schoolDaoService.getGeneralSearchableInformation(e));
				additionalSearchableInformation.stream().map(f -> {
					return new FinderEntity(e.getSchoolName(), f, Stream.of(f).map(g -> {
						String[] splitString = g.split(" ");
						List<String> resultsFinal = new ArrayList<>();
						for (String s : splitString) {
							String[] splittedByDash = s.split("-");
							for (String splittedDash : splittedByDash) {
								resultsFinal.add(splittedDash);
							}
							resultsFinal.add(s);

						}
						String[] toReturn = new String[resultsFinal.size()];
						toReturn = resultsFinal.toArray(toReturn);
						return toReturn;
					}).map(g -> Stream.of(g).map(h -> new FinderEntitySearchConstraint(h, ""))
							.collect(Collectors.toList()))
							.flatMap(List::stream).collect(Collectors.toList()), e.getLongitude(), e.getLatitude());
				}).forEach(f -> {
					this.add(f);
					e.getProjects().forEach(project -> {
						if (!this.finderEntityForProjectsMap.containsKey(project.getId())) {
							this.finderEntityForProjectsMap.put(project.getId(), new CopyOnWriteArrayList<>());
						}
						this.finderEntityForProjectsMap.get(project.getId()).add(f);
					});
				});
			});
		} finally {
			writeLock.unlock();
		}

	}

	private void checkAndUpdateEntryList() {
		var lastUpdateInDb = schoolRepo.findTopByLastUpdatedOnNotNullOrderByLastUpdatedOnDesc();
		if (this.isEmpty() || lastCachedUpdate == null || lastUpdateInDb == null
				|| lastCachedUpdate.isBefore(lastUpdateInDb.getLastUpdatedOn())) {
			this.buildEntryList();
			if (lastUpdateInDb != null) {
				log.info("Database was modified at {}, last cache update was on {}", lastUpdateInDb.getLastUpdatedOn(),
						lastCachedUpdate);
				lastCachedUpdate = lastUpdateInDb.getLastUpdatedOn();
			} else {
				log.info("Database modification date not persistent yet, last cache update was on {}",
						lastCachedUpdate);
			}
		}
	}

	@Override
	public void clear() {
		var writeLock = entryListLock.writeLock();
		writeLock.lock();
		try {
			super.clear();
			this.finderEntityForProjectsMap.clear();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Stream<FinderEntity> stream() {
		var readLock = entryListLock.readLock();
		checkAndUpdateEntryList();
		readLock.lock();
		try {
			var unmodifiableStream = super.stream().collect(Collectors.toUnmodifiableList()).stream();
			return unmodifiableStream;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public FinderEntity get(int index) {
		var readLock = entryListLock.readLock();
		checkAndUpdateEntryList();
		readLock.lock();
		try {
			var result = super.get(index);
			return result;
		} finally {
			readLock.unlock();
		}
	}

	public List<FinderEntity> sublistOfProjectId(long projectId) {
		var readLock = entryListLock.readLock();
		checkAndUpdateEntryList();
		readLock.lock();
		try {
			var sublist = this.finderEntityForProjectsMap.get(projectId);
			return sublist;
		} finally {
			readLock.unlock();
		}
	}

}
