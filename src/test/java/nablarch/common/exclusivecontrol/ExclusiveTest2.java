package nablarch.common.exclusivecontrol;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 排他テスト2
 */
@Entity
@Table(name = "EXCLUSIVE_TEST_2")
public class ExclusiveTest2 {
    
    public ExclusiveTest2() {
    };
    
    public ExclusiveTest2(Long id, Long version) {
        this.id = id;
        this.version = version;
    }

    @Id
    @Column(name = "ID", length = 10, nullable = false)
    public Long id;
    
    @Column(name = "VERSION", length = 10, nullable = false)
    public Long version;
}
