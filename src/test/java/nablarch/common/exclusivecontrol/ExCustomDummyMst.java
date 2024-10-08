package nablarch.common.exclusivecontrol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 排他ダミーマスタ
 */
@Entity
@Table(name = "EX_CUSTOM_DUMMY_MST")
public class ExCustomDummyMst {
    
    public ExCustomDummyMst() {
    };
    
    public ExCustomDummyMst(String pk1, Long version) {
        this.pk1 = pk1;
        this.version = version;
    }

    @Id
    @Column(name = "PK1", length = 6, nullable = false)
    public String pk1;
    
    @Column(name = "VERSION", length = 10, nullable = false)
    public Long version;
}
