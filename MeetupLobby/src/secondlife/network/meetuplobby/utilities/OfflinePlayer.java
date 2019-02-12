package secondlife.network.meetuplobby.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class OfflinePlayer {

    private String username;
    private UUID uuid;

    @Override
    public boolean equals(Object object) {
        if(object != null && object instanceof OfflinePlayer) {
            OfflinePlayer other = (OfflinePlayer) object;

            if(this.username != null && this.uuid != null && other.getUsername() != null && other.getUuid() != null && this.username.equals(other.getUsername()) && this.uuid.equals(other.getUuid())) {
                return true;
            }
        }

        return false;
    }
}
