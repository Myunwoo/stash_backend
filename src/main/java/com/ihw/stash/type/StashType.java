package com.ihw.stash.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StashType {
    TITLE_MAX_LENGTH(30),
    DESCRIPTION_MAX_LENGTH(60);

    private final int value;
}
