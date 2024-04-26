from flask import Flask, request, jsonify

import torch
import torch.nn as nn
import numpy as np
from transformers import AlbertForSequenceClassification, AlbertTokenizer

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

class ALBertTextClassifier(nn.Module):
    def __init__(self, num_labels=2):
        super(ALBertTextClassifier, self).__init__()
        self.albert = AlbertForSequenceClassification.from_pretrained('albert-base-v2', num_labels=num_labels)

    def forward(self, input_ids, attention_mask):
        outputs = self.albert(input_ids, attention_mask=attention_mask)
        logits = outputs.logits
        return logits
    
def load_pretrained_albert_weights(model, model_path):
    # 미리 학습된 ALBERT 모델 로드
    pretrained_model = torch.load(model_path, map_location=torch.device('cpu'))

    # ALBERT 모델의 가중치를 현재 모델로 복사
    model.load_state_dict(pretrained_model['model_state_dict'])

def test_model(model_path, keyword_list):
    # 모델 로드
    loaded_model = ALBertTextClassifier(num_labels=2)
    load_pretrained_albert_weights(loaded_model, model_path)

    # 토크나이저 로드
    tokenizer = AlbertTokenizer.from_pretrained('albert-base-v2')

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

model_path = "/app/src/AlbertForSequenceClassification.pt"
# model_path = "./AlbertForSequenceClassification.pt"

if __name__ == '__main__':
    app.run(debug=True)