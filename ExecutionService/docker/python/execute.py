import sys
import subprocess

def execute_python_code(code):
    try:
        # Write the input code to a temporary file
        with open("script.py", "w") as script_file:
            script_file.write(code)

        # Run the script and capture output
        result = subprocess.run(
            ["python", "script.py"],
            text=True,
            capture_output=True,
            timeout=10  # Set timeout for execution
        )

        # Check for runtime errors
        if result.returncode != 0:
            return f"Error: {result.stderr.strip()}"

        # Return successful output
        return result.stdout.strip()

    except subprocess.TimeoutExpired:
        return "Error: Code execution timed out."
    except Exception as e:
        return f"Error: {str(e)}"

# Main execution
if __name__ == "__main__":
    code = sys.stdin.read().strip()
    if not code:
        print("Error: No Python code provided.")
        sys.exit(1)
    print(execute_python_code(code))
