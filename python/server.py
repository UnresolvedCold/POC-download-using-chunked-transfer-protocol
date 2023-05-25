from flask import Flask, Response
import os

app = Flask(__name__)

from flask import Response

from flask import Flask, Response, stream_with_context
import os

app = Flask(__name__)

@app.route('/download')
def download_file():
    # This is the file we want to download whenever a client requests
    file_dir = 'python/public/stream/data/'
    file_name = "heuristics.bin"
    file_path = file_dir + file_name
    chunk_size = 128*1024*1024  # Adjust the chunk size as per your requirements
    
    def generate():
        with open(file_path, 'rb') as file:
            while True:
                chunk = file.read(chunk_size)
                if not chunk:
                    break
                yield chunk

    response = Response(
        stream_with_context(generate()),
        mimetype='application/octet-stream'
    )
    response.headers.set('Content-Disposition', 'attachment', filename=file_name)
    response.headers.set('X-Chunk-Size', str(chunk_size))  # Add chunk size as a header

    return response

@app.route('/download_whole')
def download_file_whole():
    # This is the file we want to download whenever a client requests
    file_dir = 'python/public/stream/data/'
    file_name = "heuristics.bin"
    file_path = file_dir + file_name

    def generate():
        with open(file_path, 'rb') as file:
            data = file.read()
            yield data

    response = Response(
        stream_with_context(generate()),
        mimetype='application/octet-stream'
    )
    response.headers.set('Content-Disposition', 'attachment', filename=file_name)

    return response


if __name__ == '__main__':
    app.run()
