package nablarch.common.exclusivecontrol;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 排他テスト1
 */
@Entity
@Table(name = "EXCLUSIVE_TEST_1")
public class ExclusiveTest1 {
    
    public ExclusiveTest1() {
    };
    
    public ExclusiveTest1(Long id, Long version) {
        this.id = id;
        this.version = version;
    }

    @Id
    @Column(name = "ID", length = 10, nullable = false)
    public Long id;
    
    @Column(name = "VERSION", length = 10, nullable = false)
    public Long version;
}
