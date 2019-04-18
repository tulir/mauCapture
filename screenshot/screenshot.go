package screenshot

import (
	"errors"
	"fmt"
	"os"
	"os/exec"
	"time"
)

var UnsupportedPlatformError = errors.New("platform not supported")
var UserCancelledOperation = errors.New("user cancelled screenshot")

type screenshotter func(filename string) (string, error)

func Screenshot() (string, error) {
	filename := fmt.Sprintf("/tmp/maucapture-%d.png", time.Now().UnixNano())
	return screenshot(filename)
}

func makeGenericScreenshotter(program string, args ...string) screenshotter {
	return func(filename string) (string, error) {
		localArgs := append(args, filename)
		err := runGenericScreenshot(program, localArgs...)
		if err != nil {
			return "", err
		}
		return checkFile(filename)
	}
}

func checkFile(filename string) (string, error) {
	_, err := os.Stat(filename)
	if err != nil {
		if os.IsNotExist(err) {
			return "", UserCancelledOperation
		}
		return "", err
	}
	return filename, nil
}

func runGenericScreenshot(program string, args ...string) error {
	path, err := exec.LookPath(program)
	if err != nil {
		return UnsupportedPlatformError
	}
	cmd := exec.Command(path, args...)
	return cmd.Run()
}
