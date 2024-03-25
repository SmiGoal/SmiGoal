FROM openjdk:17-bullseye

ARG JAR_FILE=build/libs/server-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} ./app.jar

# Chrome 설치를 위한 필수 라이브러리 설치
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    libglib2.0-0 \
    libnss3 \
    libnspr4 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdbus-1-3 \
    libxkbcommon0 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    libgbm1 \
    libpango-1.0-0 \
    libcairo2 \
    libasound2 \
    libatspi2.0-0 \
    libgtk-3-0

# Chrome 다운로드 및 설치
RUN wget https://storage.googleapis.com/chrome-for-testing-public/123.0.6312.58/linux64/chrome-linux64.zip \
    && unzip chrome-linux64.zip -d /opt/chrome \
    && rm chrome-linux64.zip

# ChromeDriver 다운로드 및 설치
RUN wget https://storage.googleapis.com/chrome-for-testing-public/123.0.6312.58/linux64/chromedriver-linux64.zip \
    && unzip chromedriver-linux64.zip -d /usr/local/bin/ \
    && rm chromedriver-linux64.zip

# Chrome 실행을 위한 환경 변수 설정
ENV DISPLAY=:99

ENTRYPOINT ["java", "-jar", "./app.jar"]
