import re
from datetime import datetime



def calculate_total_time(file_path):
    with open(file_path, 'r') as file:
        data = file.read()

    time_pattern = re.compile(r'\d{2}/\d{2} :.*= (\d{2}:\d{2})')
    times = re.findall(time_pattern, data)

    total_seconds = sum(int(t.split(':')[0])*3600 + int(t.split(':')[1])*60 for t in times)
    hours = total_seconds // 3600
    minutes = (total_seconds % 3600) // 60

    return f"Total time: {str(hours).zfill(2)}:{str(minutes).zfill(2)}h"



print(calculate_total_time('work-tracking_time.md'))