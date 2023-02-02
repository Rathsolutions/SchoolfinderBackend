/*-
 * #%L
 * SchuglemapsBackend
 * %%
 * Copyright (C) 2020 Rathsolutions. <info@rathsolutions.de>
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
package de.rathsolutions.util.finder.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.rathsolutions.util.finder.pojo.FinderEntity;
import de.rathsolutions.util.finder.pojo.FinderEntitySearchConstraint;

@Service
public class LevenstheinDistanceUtil {
	/**
	 * This method returns the most matching elements computed by levenstheins
	 * distance. If there is one perfect matching element (distance equal to 0),
	 * only this single element will be returned
	 * 
	 * @param requestString             the request string which should be used
	 *                                  while computing the distance
	 * @param resultList                the filtered list containing all possible
	 *                                  osm entities from which you want to find the
	 *                                  closest
	 * @param amount                    the amount of how many elements should be
	 *                                  returned
	 * @param computeWithSecondaryValue should the secondary value be used while
	 *                                  calculation?
	 * @return a list containing all elements
	 */
	public List<FinderEntity> computeLevenstheinDistance(String requestString, Collection<FinderEntity> resultList,
			int amount, boolean computeWithSecondaryValue, boolean perfectMatchAllowed) {
		return computeLevenstheinDistanceInternal(requestString, resultList, amount, computeWithSecondaryValue,
				perfectMatchAllowed);
	}

	private List<FinderEntity> computeLevenstheinDistanceInternal(String requestString,
			Collection<FinderEntity> resultList, int amount, boolean computeWithSecondaryValue,
			boolean perfectMatchAllowed) {
		Map<FinderEntitySearchConstraint, Integer> entityDistanceMapping = new HashMap<>();
		Map<FinderEntitySearchConstraint, FinderEntity> finderEntityDistanceMapping = new HashMap<>();
		resultList.stream().forEach(e -> {
			e.getQueryValues().forEach(f -> {
				finderEntityDistanceMapping.put(f, e);
				entityDistanceMapping.put(f, getLevenstheinDistance(requestString.toLowerCase(),
						f.getPrimaryValue().toLowerCase() + (computeWithSecondaryValue ? f.getSecondaryValue() : "")));
			});
		});
		if (requestString.equalsIgnoreCase("Brombach")) {
			System.out.println("Found");
		}
		List<Entry<FinderEntitySearchConstraint, Integer>> collect = entityDistanceMapping.entrySet().stream()
				.sorted((e, f) -> e.getValue().compareTo(f.getValue())).collect(Collectors.toList());
		List<Entry<FinderEntitySearchConstraint, Integer>> perfectMatch = collect.stream()
				.filter(e -> e.getValue() == 0 && getLevenstheinDistance(requestString.toLowerCase(),
						e.getKey().getPrimaryValue() + e.getKey().getSecondaryValue()) == 0)
				.collect(Collectors.toList());
		if (!perfectMatch.isEmpty() && perfectMatch.size() == 1 && perfectMatchAllowed) {
			return Arrays.asList(finderEntityDistanceMapping.get(perfectMatch.get(0).getKey()));
		} else {
			collect = collect.size() <= amount ? collect.subList(0, collect.size()) : collect.subList(0, amount);
			List<FinderEntity> toReturn = new ArrayList<>();
			Set<String> alreadyAdded = new HashSet<>();
			collect.stream().sorted((e, f) -> e.getValue().compareTo(f.getValue())).map(e -> e.getKey())
					.map(e -> finderEntityDistanceMapping.get(e)).forEach(e -> {
//						if (!alreadyAddedPrimary.contains(e.getPrimaryValue() + "#" + e.getSecondaryValue())) {
//							toReturn.add(e);
//							alreadyAddedPrimary.add(e.getPrimaryValue() + "#" + e.getSecondaryValue());
//						}
						boolean add = true;
						for (String elementAlreadyAdded : alreadyAdded) {
							String[] splittedLatLong = elementAlreadyAdded.split("#");
							double lat = Double.valueOf(splittedLatLong[0]);
							double lon = Double.valueOf(splittedLatLong[1]);
							if (Math.abs(lat - e.getLatVal()) < 0.001 && Math.abs(lon - e.getLongVal()) < 0.001) {
								add = false;
							}
						}
						if (add) {
							toReturn.add(e);
							alreadyAdded.add(e.getLatVal() + "#" + e.getLongVal());
						}
					});
			return toReturn;
		}
	}

	private int getLevenstheinDistance(String requestString, String entityTwo) {
		requestString = " " + requestString;
		entityTwo = " " + entityTwo;
		int[][] levenstheinMatrix = new int[requestString.length()][entityTwo.length()];
		for (int i = 0; i < requestString.length(); i++) {
			levenstheinMatrix[i][0] = i;
		}
		for (int j = 0; j < entityTwo.length(); j++) {
			levenstheinMatrix[0][j] = j;
		}
		for (int i = 1; i < requestString.length(); i++) {
			for (int j = 1; j < entityTwo.length(); j++) {
				if (requestString.charAt(i) == entityTwo.charAt(j)) {
					levenstheinMatrix[i][j] = levenstheinMatrix[i - 1][j - 1];
				} else {
					int replacementVal = levenstheinMatrix[i - 1][j - 1] + 1;
					int deletionVal = levenstheinMatrix[i - 1][j] + 1;
					int insertionVal = levenstheinMatrix[i][j - 1] + 1;
					levenstheinMatrix[i][j] = smallestIntOfThree(replacementVal, insertionVal, deletionVal);
				}
			}
		}
		return levenstheinMatrix[requestString.length() - 1][entityTwo.length() - 1];
	}

	private int smallestIntOfThree(int a, int b, int c) {
		if (a <= b && a <= c) {
			return a;
		} else if (b <= c && b <= a) {
			return b;
		} else {
			return c;
		}
	}
}
