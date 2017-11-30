import java.time.Instant;

/**
 * Created by tasos on 29/11/2017.
 */
public class Transaction {
    private String date;
    private String txId;
    private Instant time;

    public Transaction(String date, String txId, Instant time) {
        this.date = date;
        this.txId = txId;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
