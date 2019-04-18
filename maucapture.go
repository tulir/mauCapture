package main

import (
	"fmt"
	"image"
	"os"

	"fyne.io/fyne"
	"fyne.io/fyne/app"
	"fyne.io/fyne/canvas"
	"fyne.io/fyne/widget"

	"maunium.net/go/maucapture/config"

	flag "maunium.net/go/mauflag"

	"maunium.net/go/maucapture/res"
	"maunium.net/go/maucapture/screenshot"
)

const Version = "3.0"

func main() {
	var configFile = flag.MakeFull("c", "config", "Path to mauCapture config file.", "").String()
	var wantHelp, _ = flag.MakeHelpFlag()
	err := flag.Parse()
	if err != nil {
		_, _ = fmt.Fprintln(os.Stderr, err)
		flag.PrintHelp()
		os.Exit(1)
	} else if *wantHelp {
		flag.PrintHelp()
		os.Exit(0)
	}

	cfg := readConfig(*configFile)
	fmt.Println(cfg)

	img := takeScreenshot()
	if img == nil {
		os.Exit(0)
	}

	imageCanvas := &canvas.Image{
		Image:    img,
		FillMode: canvas.ImageFillOriginal,
	}
	size := img.Bounds().Size()
	imageCanvas.Resize(fyne.NewSize(size.X, size.Y))

	mc := app.New()
	mc.SetIcon(res.MauCapture)

	box := widget.NewVBox(
		widget.NewScrollContainer(imageCanvas),
		widget.NewButton("Quit", mc.Quit),
	)
	box.Resize(fyne.NewSize(400, 400))
	w := mc.NewWindow("mauCapture " + Version)
	w.Resize(fyne.NewSize(400, 400))
	w.SetContent(box)

	w.ShowAndRun()
}

func readConfig(path string) *config.Config {
	if len(path) == 0 {
		path = config.GetDefaultFile()
	}
	cfg, err := config.Open(path)
	if err != nil {
		showFatalError("Unhandled error", err.Error())
	}
	return cfg
}

func takeScreenshot() image.Image {
	path, err := screenshot.Screenshot()
	if err == screenshot.UnsupportedPlatformError {
		showFatalError("Unsupported Platform", "You do not have any supported screenshot programs.\n" +
			"Please define your own screenshot program in the config file.")
	} else if err == screenshot.UserCancelledOperation {
		return nil
	} else if err != nil {
		showFatalError("Unhandled error", err.Error())
	}

	reader, err := os.OpenFile(path, os.O_RDONLY, 0644)
	if err != nil {
		showFatalError("Unhandled error", err.Error())
	}
	img, _, err := image.Decode(reader)
	if err != nil {
		showFatalError("Unhandled error", err.Error())
	}
	return img
}

func showFatalError(title, message string) {
	err := app.New()
	err.SetIcon(res.MauCapture)

	w := err.NewWindow(title)
	w.SetContent(fyne.NewContainer(
		widget.NewLabel(message),
		widget.NewButton("OK", err.Quit),
	))

	w.ShowAndRun()
	os.Exit(2)
}
