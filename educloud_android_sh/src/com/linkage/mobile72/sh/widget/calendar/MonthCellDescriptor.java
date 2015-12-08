// Copyright 2012 Square, Inc.

package com.linkage.mobile72.sh.widget.calendar;

import java.util.Date;

/** Describes the state of a particular date cell in a {@link MonthView}. */
class MonthCellDescriptor {
  public enum RangeState {
    NONE, FIRST, MIDDLE, LAST
  }
  public static final int KQ_NORMAL = 2;
  public static final int KQ_QINGJIA = 1;
  public static final int KQ_QUEQIN = 3;
  
  private final Date date;
  private final int value;
  private final boolean isCurrentMonth;
  private boolean isSelected;
  private final boolean isToday;
  private final boolean isSelectable;
  private boolean isHighlighted;
  private RangeState rangeState;
  //我加的
  private int kaoqinState;

  MonthCellDescriptor(Date date, boolean currentMonth, boolean selectable, boolean selected,
      boolean today, boolean highlighted, int value, RangeState rangeState, int kaoqinState) {
    this.date = date;
    isCurrentMonth = currentMonth;
    isSelectable = selectable;
    isHighlighted = highlighted;
    isSelected = selected;
    isToday = today;
    this.value = value;
    this.rangeState = rangeState;
    this.kaoqinState = kaoqinState;
  }

  public Date getDate() {
    return date;
  }

  public boolean isCurrentMonth() {
    return isCurrentMonth;
  }

  public boolean isSelectable() {
    return isSelectable;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  boolean isHighlighted() {
    return isHighlighted;
  }

  void setHighlighted(boolean highlighted) {
    isHighlighted = highlighted;
  }

  public boolean isToday() {
    return isToday;
  }

  public RangeState getRangeState() {
    return rangeState;
  }

  public void setRangeState(RangeState rangeState) {
    this.rangeState = rangeState;
  }

  public int getKaoqinState() {
	return kaoqinState;
}

public void setKaoqinState(int kaoqinState) {
	this.kaoqinState = kaoqinState;
}

public int getValue() {
    return value;
  }

  @Override public String toString() {
    return "MonthCellDescriptor{"
        + "date="
        + date
        + ", value="
        + value
        + ", isCurrentMonth="
        + isCurrentMonth
        + ", isSelected="
        + isSelected
        + ", isToday="
        + isToday
        + ", isSelectable="
        + isSelectable
        + ", isHighlighted="
        + isHighlighted
        + ", rangeState="
        + rangeState
        + '}';
  }
}
