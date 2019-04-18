package config

import (
	"encoding/json"
	"io/ioutil"
	"os"
	"os/exec"
	"path/filepath"
)

func GetDefaultFile() string {
	dir := getDir()
	_ = os.MkdirAll(dir, 0700)
	return filepath.Join(dir, "maucapture.json")
}

func GetDefaultSaveDirectory() string {
	cmd := exec.Command("xdg-user-dir", "PICTURES")
	err := cmd.Run()
	if err != nil {
		home, _ := os.UserHomeDir()
		return home
	}
	output, err := cmd.Output()
	if err != nil || len(output) == 0 {
		home, _ := os.UserHomeDir()
		return home
	}
	return string(output)
}

type Config struct {
	Screenshotter struct {
		Command string   `json:"command"`
		Args    []string `json:"args"`
	} `json:"screenshot,omitempty"`

	SaveDirectory string `json:"save_directory"`

	MauImageServer struct {
		Server    string `json:"server"`
		Username  string `json:"username"`
		AuthToken string `json:"authtoken"`
	} `json:"mauimageserver,omitempty"`

	Matrix struct {
		HomeserverURL string `json:"homeserver_url"`
		AccessToken   string `json:"access_token"`
	} `json:"matrix,omitempty"`
}

func Open(file string) (*Config, error) {
	data, err := ioutil.ReadFile(file)
	if err != nil {
		if os.IsNotExist(err) {
			return &Config{}, nil
		}
		return nil, err
	}
	var config Config
	err = json.Unmarshal(data, &config)
	if err != nil {
		return nil, err
	}
	if len(config.SaveDirectory) == 0 {
		config.SaveDirectory = GetDefaultSaveDirectory()
	}
	return &config, nil
}
