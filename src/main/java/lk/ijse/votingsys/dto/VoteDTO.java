package lk.ijse.votingsys.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VoteDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String option;
    private int optionACount;
    private int optionBCount;
    private int optionCCount;

    public VoteDTO(String username, String option) {
        this.username = username;
        this.option = option;
    }

    public void addCount(String option){
        switch (option){
            case "A" -> optionACount++;
            case "B" -> optionBCount++;
            case "C" -> optionCCount++;
        }
    }
}
