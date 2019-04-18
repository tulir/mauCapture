package config

import (
	"os"
)

func getDir() string {
	configDir = os.Getenv("APPDATA")
	if len(configDir) != 0 {
		return configDir
	}
	configDir, _ = os.UserHomeDir()
	return configDir
}
