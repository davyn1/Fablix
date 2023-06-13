import sys

# Check if at least one command-line argument is provided
# sys.argv counts the script itself as an argument
if len(sys.argv) < 2:
    print("Not enough inputs")
    sys.exit(1)

# Iterate over the command-line arguments starting from the second one
# Variables to store all time values across files
all_time1 = []
all_time2 = []

# Iterate over the command-line arguments starting from the second one
for file_path in sys.argv[1:]:
    try:
        # Open the file for reading
        with open(file_path, 'r') as file:
            # Read the contents of the file
            file_contents = file.read()

            # Additional logic to calculate time1 and time2
            time1 = []
            time2 = []

            for line in file_contents.split('\n'):
                if line.strip() != '':
                    data = line.split(",")
                    time1.append(int(data[0].strip()))
                    time2.append(int(data[1].strip()))

            avg_time1 = sum(time1) / len(time1) / 1000000
            avg_time2 = sum(time2) / len(time2) / 1000000

            # Append the time values to the overall lists
            all_time1.extend(time1)
            all_time2.extend(time2)

    except FileNotFoundError:
        print(f"File not found: {file_path}")

# Calculate and print the average values across all files
avg_all_time1 = sum(all_time1) / len(all_time1) / 1000000
avg_all_time2 = sum(all_time2) / len(all_time2) / 1000000

print("Average TS", avg_all_time1)
print("Average TJ", avg_all_time2)