from flask import Flask, request, jsonify

import torch
import torch.nn as nn
import pandas as pd
from sklearn.model_selection import train_test_split
from transformers import BertTokenizer
from torch.utils.data import Dataset, DataLoader
from transformers import BertForSequenceClassification, AdamW
import numpy as np

app = Flask(__name__)

@app.route('/test', methods=['POST'])
def predict():
    data = request.json
    result = test_model(model_path, data)
    return jsonify(result)

# model = torch.load(model_path, map_location=torch.device('cpu'))
# 모델 수정사항 map_location=torch.device('cpu')를 추가하여 로컬에서 동작하도록 수정
# 분류 결과값 반환하도록 설정

class BertTextClassifier(nn.Module):
    def __init__(self, num_labels=2):
        super(BertTextClassifier, self).__init__()
        self.bert = BertForSequenceClassification.from_pretrained('bert-base-multilingual-cased', num_labels=num_labels)

    def forward(self, input_ids, attention_mask):
        outputs = self.bert(input_ids, attention_mask=attention_mask)
        logits = outputs.logits
        return logits

def load_pretrained_bert_weights(model, pretrained_model_name='bert-base-multilingual-cased'):
    # 미리 학습된 BERT 모델 로드
    pretrained_bert_model = BertTextClassifier(num_labels=2)  # BERT 모델 대신 사용자 모듈의 BertTextClassifier 클래스로 변경
    pretrained_bert_model.load_state_dict(torch.load(pretrained_model_name, map_location=torch.device('cpu'))['model_state_dict'])

    # BERT 모델의 가중치를 현재 모델로 복사
    model.bert.load_state_dict(pretrained_bert_model.bert.state_dict(), strict=False)
def test_model(model_path, keyword_list):
    # 모델 로드
    loaded_model = BertTextClassifier(num_labels=2)
    load_pretrained_bert_weights(loaded_model, model_path)

    # 토크나이저 로드
    tokenizer = BertTokenizer.from_pretrained('bert-base-multilingual-cased')

    # 모델을 평가 모드로 설정
    loaded_model.eval()

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
    
    return result

model_path = "/model/model_100000_data.pt"
general_keywords = ["사람", "시간", "일", "때", "그냥", "말", "것", "생각", "일어나다", "일어나다", "이렇게",
                    "저렇게", "그렇게", "곳", "많다", "적다", "어떻다", "모르다", "알다", "하다", "되다",
                    "가다", "오다", "있다", "없다", "보다", "듣다", "많이", "조금", "자주", "가끔", "어제",
                    "오늘", "내일", "이번주", "다음주"]
keyword_list = ["winter", "비상", "긴급", "즉시", "신속", "급한", "즉각",
                     "당첨", "추첨", "상금", "무료", "특별 이벤트",
                     "해킹", "보안", "계정", "비밀번호", "변경",
                     "확인", "인증", "업데이트", "갱신", "확인 필요",
                     "공지", "공식", "긴급 조치", "중요", "고객센터",
                     "혜택", "특가", "할인", "이벤트", "무료 증정",
                     "마감", "기한", "오늘까지", "한정", "마지막 기회",
                     "당신의", "회원", "고객", "고객님", "님"]
finance_smishing_keywords = ["투자", "환급", "보험", "자동이체", "소액결제", "거래", "무료", "특별", "당첨",
                             "무료체험", "무료제공", "이벤트", "당첨", "상품권", "미포함", "청구", "승인", "신용",
                                 "사람", "시간", "일", "때", "그냥", "말", "것", "생각", "일어나다", "일어나다", "이렇게",
                    "저렇게", "그렇게", "곳", "많다", "적다", "어떻다", "모르다", "알다", "하다", "되다",
                    "가다", "오다", "있다", "없다", "보다", "듣다", "많이", "조금", "자주", "가끔", "어제",
                    "오늘", "내일", "이번주", "다음주"]

general_word_list = ["안녕", "반가워", "프로그래밍", "데이터", "인공지능"]

# test_model(model_path, keyword_list)

if __name__ == '__main__':
    app.run(debug=True)