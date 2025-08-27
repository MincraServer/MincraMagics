package jp.mincra.mincramagics.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity class mapping to the 'job_rewards' table.
 * Uses a single, auto-incrementing primary key and a composite index.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_rewards", indexes = {
        // Defines a composite index for faster lookups based on these three columns.
        @Index(name = "idx_player_job_level", columnList = "player_id, job_id, level")
})
@Builder
public class JobReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "job_id", nullable = false)
    private Integer jobId;

    @Column(name = "level", nullable = false)
    private Integer level;

    @CreationTimestamp
    @Column(name = "received_at", nullable = false, updatable = false) // updatable=falseで更新されないようにする
    private Instant receivedAt;
}
