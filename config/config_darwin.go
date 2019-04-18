package config

import (
	"os"
	"path/filepath"
)

func getDir() {
	home, _ := os.UserHomeDir()
	return filepath.Join(env, "Library", "Application Support", "mauCapture")
}
