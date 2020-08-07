# Notes

## ngrep

```bash
sudo ngrep -d lo0 -t '' 'tcp and port 7774'

echo -n "Hello World" | netcat 127.0.0.1 6667


SET A HELLO
=>

*3\r\n
$3\r\n
SET\r\n
$1\r\n
A\r\n
$5\r\n
hello\r\n
```