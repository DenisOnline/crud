package pet.project.shulzhenko.crud.utils;

import lombok.Getter;

@Getter
public enum CustomClaims {

    TYPE("type");

    private final String message;

    CustomClaims(String message) { this.message = message; }

}