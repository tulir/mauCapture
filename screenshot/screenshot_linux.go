// +build !darwin,!windows

package screenshot

import (
	"os/exec"
)

var linuxScreenshotters = map[string]screenshotter{
	"gnome":     makeGenericScreenshotter("gnome-screenshot", "--area", "--file"),
	"spectacle": makeGenericScreenshotter("spectacle", "--background", "--nonotify", "--region", "--output"),
	"shutter":   makeGenericScreenshotter("shutter", "--select", "--exit_after_capture", "--no_session", "--output"),
	"import":    makeGenericScreenshotter("import"),
	"maim":      makeGenericScreenshotter("maim", "--select"),
	"scrot":     makeGenericScreenshotter("scrot", "--select"),
	"xfce4":     xfce4Screenshotter,
}

var screenshotOrder = []string{"spectacle", "gnome", "xfce4", "maim", "scrot", "shutter", "import"}

func screenshot(filename string) (string, error) {
	for _, platform := range screenshotOrder {
		filename, err := linuxScreenshotters[platform](filename)
		if err != UnsupportedPlatformError {
			return filename, err
		}
	}
	return "", UnsupportedPlatformError
}

func xfce4Screenshotter(_ string) (string, error) {
	path, err := exec.LookPath("xfce4-screenshoter")
	if err != nil {
		return "", UnsupportedPlatformError
	}
	cmd := exec.Command(path, "--region", "--open", "echo")
	err = cmd.Run()
	if err != nil {
		return "", err
	}
	output, err := cmd.Output()
	if err != nil {
		return "", err
	}
	return checkFile(string(output))
}
