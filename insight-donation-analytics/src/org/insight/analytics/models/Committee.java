package org.insight.analytics.models;

public class Committee {
    private String committeeId;
    private String zipCode;
    private int year;
    
    public Committee(String committeeId, String zipCode, int i) {
        super();
        this.committeeId = committeeId;
        this.zipCode = zipCode;
        this.year = i;
    }
    
    public String getCommitteeId() {
        return committeeId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public int getYear() {
        return year;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((committeeId == null) ? 0 : committeeId.hashCode());
        result = prime * result + year;
        result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Committee other = (Committee) obj;
        if (committeeId == null) {
            if (other.committeeId != null)
                return false;
        } else if (!committeeId.equals(other.committeeId))
            return false;
        if (year != other.year)
            return false;
        if (zipCode == null) {
            if (other.zipCode != null)
                return false;
        } else if (!zipCode.equals(other.zipCode))
            return false;
        return true;
    }

    
    
    
}
