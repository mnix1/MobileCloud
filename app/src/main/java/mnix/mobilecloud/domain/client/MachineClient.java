package mnix.mobilecloud.domain.client;

import com.orm.SugarRecord;

import mnix.mobilecloud.MachineRole;

public class MachineClient extends SugarRecord {
    private String identifier;
    private MachineRole role;

    public MachineClient() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public MachineRole getRole() {
        return role;
    }

    public void setRole(MachineRole role) {
        this.role = role;
    }
}
