const fs = require('fs');
const { exec } = require('child_process');

function executeNodeCode(code) {
    // Write the input JavaScript code to a file
    fs.writeFileSync("script.js", code);

    // Run the script
    exec('node script.js', (error, stdout, stderr) => {
        if (error) {
            console.error(`Error: ${stderr}`);
            return;
        }
        console.log(stdout);
    });
}

// Main execution
const code = process.stdin.read().trim();
if (!code) {
    console.log("Error: No Node.js code provided.");
    process.exit(1);
}
executeNodeCode(code);
