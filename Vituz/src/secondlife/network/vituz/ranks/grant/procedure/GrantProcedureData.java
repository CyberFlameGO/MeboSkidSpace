package secondlife.network.vituz.ranks.grant.procedure;

import lombok.Getter;
import lombok.Setter;
import secondlife.network.vituz.ranks.Rank;

@Getter
@Setter
public class GrantProcedureData {
	
	private GrantProcedureStage stage;
	private Rank rank;
	private String reason;
	private long created;
	private long duration;

	public GrantProcedureData() {
		this.stage = GrantProcedureStage.RANK;
	}
}
