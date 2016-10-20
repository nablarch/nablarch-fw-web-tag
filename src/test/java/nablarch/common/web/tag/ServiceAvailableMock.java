package nablarch.common.web.tag;

import nablarch.common.availability.ServiceAvailability;

public class ServiceAvailableMock implements ServiceAvailability {

    private boolean available;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable(String requestId) {
        return available;
    }
}