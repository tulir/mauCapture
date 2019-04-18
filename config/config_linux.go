// +build !windows,!darwin

package config

import (
	"os"
	"path/filepath"
)

func getDir() string {
	configDir := os.Getenv("XDG_CONFIG_HOME")
	if len(configDir) != 0 {
		return configDir
	}
	home, err := os.UserHomeDir()
	if err == nil {
		return filepath.Join(home, ".config")
	}
	return "/"
}
