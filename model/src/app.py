from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/test', methods=['POST'])
def predict():
    # 여기에 모델을 로드하고 예측을 수행하는 코드를 작성
    data = request.json
    print(data)
    if len(str(data['data'])) >= 10:
        result = "true"
    else:
        result = "false"
    return jsonify(result)

if __name__ == '__main__':
    app.run(debug=True)