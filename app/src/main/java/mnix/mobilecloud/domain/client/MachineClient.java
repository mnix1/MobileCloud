package mnix.mobilecloud.domain.client;

import com.google.gson.Gson;
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

    public String toParams() {
        return "identifier=" + identifier + "&role=" + role.toString();
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
