package nablarch.common.exclusivecontrol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 排他ユーザマスタ2
 */
@Entity
@Table(name = "EXCLUSIVE_USER_MST2")
public class ExclusiveUserMst2 {
    
    public ExclusiveUserMst2() {
    };
    
    public ExclusiveUserMst2(String userId, Long version) {
        this.userId = userId;
        this.version = version;
    }

    @Id
    @Column(name = "USER_ID", length = 6, nullable = false)
    public String userId;
    
    @Column(name = "VERSION", length = 10, nullable = false)
    public Long version;
}
