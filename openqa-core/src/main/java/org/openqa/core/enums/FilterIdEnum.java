package org.openqa.core.enums;

public enum FilterIdEnum {
	AnswerTypeFilter("Answer type testing"),
	AnswerPatternFilter("Pattern learning and matching"),
	FactoidsFromPredicatesFilter("Semantic parsing");
	private final String IdString;

	private FilterIdEnum(String IdString) {
		this.IdString = IdString;
	}

	/**
	 * @return the idString
	 */
	public String getIdString() {
		return IdString;
	}

}
