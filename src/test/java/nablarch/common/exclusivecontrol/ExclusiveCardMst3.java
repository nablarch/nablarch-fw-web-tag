package nablarch.common.exclusivecontrol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 排他カードマスタ3
 */
@Entity
@Table(name = "EXCLUSIVE_CARD_MST3")
public class ExclusiveCardMst3 {
    
    public ExclusiveCardMst3() {
    };
    
    public ExclusiveCardMst3(String cardId, Long version) {
        this.cardId = cardId;
        this.version = version;
    }

    @Id
    @Column(name = "CARD_ID", length = 6, nullable = false)
    public String cardId;
    
    @Column(name = "VERSION", length = 10, nullable = false)
    public Long version;
}
