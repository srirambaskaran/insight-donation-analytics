package org.insight.analytics.pojo;

public class Committee {
    private String committee;
    private String zipCode;
    private int year;
    
    public Committee(String committee, String zipCode, int i) {
        super();
        this.committee = committee;
        this.zipCode = zipCode;
        this.year = i;
    }
    
    public String getCommittee() {
        return committee;
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
        result = prime * result + ((committee == null) ? 0 : committee.hashCode());
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
        if (committee == null) {
            if (other.committee != null)
                return false;
        } else if (!committee.equals(other.committee))
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
