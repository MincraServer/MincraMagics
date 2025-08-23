package jp.mincra.mincramagics.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class mapping to the 'job_rewards' table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_rewards")
@IdClass(JobRewardId.class) // Specifies the composite key class
public class JobReward {

    @Id
    @Column(name = "player_id", nullable = false)
    private Integer playerId;

    @Id
    @Column(name = "job_id", nullable = false)
    private Integer jobId;

    @Id
    @Column(name = "level", nullable = false)
    private Integer level;

    @Id
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "received_at", nullable = false)
    private String receivedAt; // Using String for simplicity as per schema (TEXT)
}
