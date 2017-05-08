package mnix.mobilecloud.domain.client;

import com.orm.SugarRecord;

import mnix.mobilecloud.MachineRole;

public class MachineClient extends SugarRecord {
    protected String identifier;
    protected MachineRole role;
    protected String device;
    protected String system;
    protected String name;
    protected Long speed;
    protected Long freeSpace;

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

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public Long getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(Long freeSpace) {
        this.freeSpace = freeSpace;
    }

    public String toParams() {
        return "identifier=" + identifier + "&role=" + role.toString() + "&device=" + device + "&system=" + system + "&name=" + name + "&speed=" + speed + "&freeSpace=" + freeSpace;
    }
}
