package smigoal.server.dto;

import lombok.Data;

@Data
public class ModelResponseDto {
    private String input_text;
    private Double ham_percentage;
    private Double spam_percentage;
    private String result;

    public ModelResponseDto(){

    }

    public ModelResponseDto(String result){
        this.result = result;
    }
}
