package mnix.mobilecloud.domain.server;

import com.orm.SugarRecord;

import java.util.Date;
import java.util.Map;

import mnix.mobilecloud.MachineRole;
import mnix.mobilecloud.domain.client.MachineClient;

public class MachineServer extends SugarRecord {
    private String identifier;
    private MachineRole role;
    private String ipAddress;
    private Date lastContact;

    public MachineServer() {
    }

    public MachineServer(MachineClient machineClient) {
        this.identifier = machineClient.getIdentifier();
        this.role = machineClient.getRole();
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
                '}';
    }
}
