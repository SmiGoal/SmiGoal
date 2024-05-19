from flask import Flask, request, jsonify

import torch
import torch.nn as nn
import numpy as np
from transformers import RobertaModel, RobertaConfig, RobertaTokenizer

from keyExtraction import extract_meaningful_words as extract

app = Flask(__name__)

@app.route('/test', methods=['POST'])
def predict():
    data = request.data.decode('utf-8')

    # 키워드 추출
    data = extract(data)

    detection_result = test_model(model_path, data)

    result_dict = {
        "input_text": detection_result.input_text,
        "ham_percentage": detection_result.ham_percentage,
        "spam_percentage": detection_result.spam_percentage,
        "result": detection_result.result,
    }

    return jsonify(result_dict)

class AnalysisResult:
    def __init__(self, input_text, ham_percentage, spam_percentage, result):
        self.input_text = input_text
        self.ham_percentage = ham_percentage
        self.spam_percentage = spam_percentage
        self.result = result

class RobertaTextClassifier(nn.Module):
    def __init__(self, num_labels=2, dropout_prob=0.2):
        super(RobertaTextClassifier, self).__init__()

        # RoBERTa 모델 및 설정 불러오기
        config = RobertaConfig.from_pretrained('roberta-base')
        self.roberta = RobertaModel.from_pretrained('roberta-base', config=config)

        # 은닉층의 크기는 config에서 가져옵니다.
        self.hidden_size = config.hidden_size

        # 라벨의 개수
        self.num_labels = num_labels

        # 활성화 함수 변경 (예: ReLU)
        self.activation = nn.ReLU()

        # 첫 번째 선형 레이어: 입력 크기는 hidden_size, 출력 크기는 hidden_size로 설정
        self.linear1 = nn.Linear(in_features=self.hidden_size, out_features=self.hidden_size)

        # 두 번째 선형 레이어: 입력 크기는 hidden_size, 출력 크기는 num_labels로 설정
        self.linear2 = nn.Linear(in_features=self.hidden_size, out_features=self.num_labels)

        # 드롭아웃 레이어 추가
        self.dropout1 = nn.Dropout(dropout_prob)
        self.dropout2 = nn.Dropout(dropout_prob)

    def forward(self, input_ids, attention_mask):
        # RoBERTa 모델의 출력 받기
        outputs = self.roberta(input_ids=input_ids, attention_mask=attention_mask)

        # Pooler 출력에서 [CLS] 토큰에 해당하는 벡터 추출
        cls_vector = outputs.pooler_output

        # 첫 번째 선형 레이어 및 드롭아웃 적용
        cls_vector = self.linear1(cls_vector)
        cls_vector = self.activation(cls_vector)
        cls_vector = self.dropout1(cls_vector)

        # 두 번째 선형 레이어 및 드롭아웃 적용
        cls_vector = self.linear2(cls_vector)
        cls_vector = self.dropout2(cls_vector)

        return cls_vector

    
def load_pretrained_roberta_weights(model, model_path):
    # 미리 학습된 ALBERT 모델 로드
    pretrained_model = torch.load(model_path)

    # ALBERT 모델의 가중치를 현재 모델로 복사
    model.load_state_dict(pretrained_model['model_state_dict'])
    
def test_model(model_path, keyword_list):
        
    # 모델 로드
    loaded_model = RobertaTextClassifier(num_labels=2)
    load_pretrained_roberta_weights(loaded_model, model_path)

    # 토크나이저 로드
    tokenizer = RobertaTokenizer.from_pretrained('roberta-base')

    # 입력 데이터 토큰화
    input_text = ' '.join(keyword_list)
    encoded_text = tokenizer(input_text, return_tensors='pt', truncation=True, padding=True)

    # 모델 예측
    with torch.no_grad():
        outputs = loaded_model(encoded_text['input_ids'], attention_mask=encoded_text['attention_mask'])

    # 확률로 변환
    probabilities = torch.nn.functional.softmax(outputs, dim=1).cpu().numpy()

    # 더 높은 확률의 클래스를 결정
    predicted_label = np.argmax(probabilities)
    # 결과 출력 (확률을 퍼센트로 변환)
    ham_prob_percentage = probabilities[0][0] * 100
    smishing_prob_percentage = probabilities[0][1] * 100

    # 결과 출력
    result = 'ham' if predicted_label == 0 else 'smishing'
    print(f"입력 데이터: {input_text}")
    print(f"모델 예측 확률 (ham, smishing): {ham_prob_percentage:.2f}% , {smishing_prob_percentage:.2f}%")
    print(f"모델 예측 결과: {result}")

    result_object = AnalysisResult(input_text, ham_prob_percentage, smishing_prob_percentage, result)

    return result_object

model_path = "/app/src/RoBERTa-base.pt"
# model_path = "./RoBERTa-base.pt"

if __name__ == '__main__':
    app.run(debug=True)