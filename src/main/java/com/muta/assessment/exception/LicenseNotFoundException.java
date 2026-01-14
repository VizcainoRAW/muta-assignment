package com.muta.assessment.exception;

public class LicenseNotFoundException extends RuntimeException {
    public LicenseNotFoundException(Long licenseId) {
        super("License not found: " + licenseId);
    }
}