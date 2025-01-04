const fs = require('fs');
const { exec } = require('child_process');

function executeNodeCode(code) {
    // Write the input JavaScript code to a file
    fs.writeFileSync('script.js', code);

    // Run the script using Node.js
    exec('node script.js', (error, stdout, stderr) => {
        if (error) {
            console.error(`Error: ${stderr || error.message}`);
            process.exit(1); // Ensure proper exit on failure
        }
        console.log(stdout); // Print the script output
        process.exit(0); // Ensure proper exit on success
    });
}

// Read from stdin
let input = '';
process.stdin.on('data', (chunk) => {
    input += chunk;
});

process.stdin.on('end', () => {
    const code = input.trim();
    if (!code) {
        console.error('Error: No Node.js code provided.');
        process.exit(1);
    }
    executeNodeCode(code);
});
