import boto3
from dotenv import load_dotenv
import os

load_dotenv()

access_key_id = os.getenv('AWS_ACCESS_KEY_ID')
secret_access_key = os.getenv('AWS_SECRET_ACCESS_KEY')

s3 = boto3.client('s3',
                  aws_access_key_id=access_key_id,
                  aws_secret_access_key=secret_access_key)

model_dir = './model_100000_data.pt'
bucket_name = 'smigoal'
s3_dir = 'model/model_100000_data.pt'

s3.upload_file(model_dir, bucket_name, s3_dir)