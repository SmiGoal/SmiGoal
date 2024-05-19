FROM nvidia/cuda:12.1.0-cudnn8-runtime-ubuntu20.04

# Set up environment variables
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
ENV PATH /usr/local/nvidia/bin:/usr/local/cuda/bin:${PATH}
ENV LD_LIBRARY_PATH /usr/local/nvidia/lib:/usr/local/nvidia/lib64:/usr/local/cuda/lib64:${LD_LIBRARY_PATH}
ENV TZ=Asia/Seoul
ENV DEBIAN_FRONTEND=noninteractive

# Set working directory
WORKDIR /app

# Install Python, pip, and tzdata for timezone configuration
RUN apt-get update && \
    apt-get install -y python3 python3-pip openjdk-11-jdk tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    apt-get clean

# Copy requirements file and install dependencies
COPY requirements.txt .
RUN pip3 install -r requirements.txt

# Set Java environment variables
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

# Copy the rest of the application code
COPY . .

# Set Flask environment variables
ENV FLASK_APP=src/app.py
ENV FLASK_RUN_HOST=0.0.0.0

# Expose port 5000
EXPOSE 5000

# Command to run the application
CMD ["flask", "run"]
