# My Java Command-Line Interface (CLI)

A simple Java-based command-line interface for manipulating system files.

## Overview

This command-line interface (CLI) is designed to simulate a standard CLI such as bash or Powershell.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)

## Getting Started

To get started with this CLI tool, follow the instructions below.

### Prerequisites

Before using the CLI, you must have the following prerequisites installed on your system:

- Java (version 1.8 or later)

### Installation

To install and use this CLI, follow these steps:

1. Clone the repository:
```git clone https://github.com/yourusername/your-project.git```

2. Run the project using Java:
```java src/Terminal.java```

## Usage

The CLI supports the following command-line options:

- `echo [args]`
- `pwd`
- `cd [dir]`
- `ls [-r]` 
- `mkdir dirs`
- `rmdir dir|*`
- `touch filename`
- `cp [-r] path1 path2`
- `rm file`
- `cat file`
- `wc file`
- `history`

the CLI also supports the following operators:
- `> filepath`  saves the output of command to filepath
- `>> filepath` appends the output to filepath

