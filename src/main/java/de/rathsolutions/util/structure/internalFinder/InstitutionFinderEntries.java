package de.rathsolutions.util.structure.internalFinder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.rathsolutions.jpa.repo.SchoolRepo;
import de.rathsolutions.util.osm.pojo.FinderEntity;
import de.rathsolutions.util.structure.AbstractEntries;

@Service
@Scope("singleton")
public class InstitutionFinderEntries extends AbstractEntries {

    private static final long serialVersionUID = 8979518050945610433L;

    @Autowired
    private SchoolRepo schoolRepo;

    /**
     * Builds the internal entry list with all searchable information from
     * institutions
     */
    @PostConstruct
    public void buildEntryList() {
	schoolRepo.findAll()
		.forEach(e -> e.getSearchableInformation().stream()
			.map(f -> new FinderEntity(f, e.getSchoolName(), e.getLongitude(), e.getLatitude()))
			.forEach(f -> this.add(f)));

    }

}
