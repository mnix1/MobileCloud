package mnix.mobilecloud.domain.server;

import java.util.Date;

import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.domain.client.MachineClient;

public class MachineServer extends MachineClient {
    private String ipAddress;
    private Date lastContact;
    private Boolean active;

    public MachineServer() {
    }

    public MachineServer(MachineClient machineClient) {
        this.identifier = machineClient.getIdentifier();
        this.role = machineClient.getRole();
        this.device = machineClient.getDevice();
        this.system = machineClient.getSystem();
        this.name = machineClient.getName();
        this.speed = machineClient.getSpeed();
        this.space = machineClient.getSpace();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getLastContact() {
        return lastContact;
    }

    public void setLastContact(Date lastContact) {
        this.lastContact = lastContact;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean isMaster() {
        return role == MachineRole.MASTER;
    }

    @Override
    public String toString() {
        return "MachineServer{" +
                "identifier='" + identifier + '\'' +
                ", role=" + role +
                ", ipAddress='" + ipAddress + '\'' +
                ", lastContact=" + lastContact +
                ", name=" + name +
                ", device=" + device +
                ", system=" + system +
                ", active=" + active +
                ", speed=" + speed +
                ", space=" + space +
                '}';
    }
}
