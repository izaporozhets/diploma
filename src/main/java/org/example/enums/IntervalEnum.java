package org.example.enums;

import java.util.Map;

public enum IntervalEnum {
	NONE("Без сповіщення", 1),
	TWICE_A_DAY("Два рази в день", 2),
	EVERY_DAY("Кожен день", 3),
	TWICE_A_WEEK("Два рази в тиждень", 4),
	THREE_TIMES_A_WEEK("Три рази в тиждень", 5);

	IntervalEnum(String description, Integer id) {
		this.description = description;
		this.id = id;
	}

	private Integer id;
	private String description;

	public Integer getId(){
		return this.id;
	}

	public String getDescription(){
		return this.description;
	}

	public static String getDescriptionById(Integer id) {
		Map<Integer, IntervalEnum> map = Map.of(1, NONE, 2, TWICE_A_DAY, 3, EVERY_DAY, 4, TWICE_A_WEEK, 5, THREE_TIMES_A_WEEK);
		return map.get(id).getDescription();
	}
	public static Integer getIdByDescription(String name) {
		Map<String, IntervalEnum> map = Map.of(NONE.getDescription(), NONE, TWICE_A_DAY.getDescription(), TWICE_A_DAY, EVERY_DAY.getDescription(), EVERY_DAY, TWICE_A_WEEK.getDescription(), TWICE_A_WEEK, THREE_TIMES_A_WEEK.getDescription(), THREE_TIMES_A_WEEK);
		return map.get(name).getId();
	}
}
