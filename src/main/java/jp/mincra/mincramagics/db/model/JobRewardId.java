package jp.mincra.mincramagics.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Represents the composite primary key for the JobReward entity.
 * This class must implement Serializable and have equals() and hashCode() methods.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRewardId implements Serializable {
    private Integer playerId;
    private Integer jobId;
    private Integer level;
    private Integer itemId;
}
