# meta-qemu-arm-a9

## Structure of directory and file

```bash
.
├── meta-qemu-arm-a9-app/
│   ├── conf/
│   ├── recipes-core/
│   │   └── images/
│   │       └── qemu-image-devel.bb
│   └── layer.conf
├── meta-qemu-arm-a9-bsp/
│   ├── conf/
│   │   ├── machine/
│   │   │   └── qemuarma9.conf
│   │   └── layer.conf
│   └── recipes-kernel/
│       └── linux-yocto/
│           ├── linux-yocto-dev.bbappend
│           └── linux-yocto_%.bbappend
├── meta-qemu-arm-a9-distro/
│   └── conf/
│       └── layer.conf
├── LICENSE
└── README.md
```

## Instruction for build

Create a folder to contain all the files that make up the BSP.

```bash
$ mkdir qemu-arm-a9 && cd qemu-arm-a9
```

Download the following repository into `qemu-arm-a9`

```bash
$ git clone https://github.com/yoctoproject/poky.git -b dunfell
$ git clone https://github.com/openembedded/meta-openembedded.git -b dunfell
$ git clone https://gitlab.com/norlando/meta-qemu-arm-a9.git -b dunfell
```

Source envirounment

```bash
$ source poky/oe-init-build-env build
```

In the `conf/bblayers.conf` file, add the layers from the repositories you downloaded previously.

In the `conf/layer.conf` file, add the machine `qemuarma9`.

For build kernel execute the follow command:

```bash
$ bitbake virtual/kernel
```

## Test

On your VM install the follow packages:

```
$ sudo apt-get update && sudo apt install -y qemu-system-arm bridge-utils \
 net-tools nfs-kernel-server uml-utilities \
```

For testing kernel, performd follow command:

```
$ qemu-system-arm -M vexpress-a9 -m 128M -nographic -kernel zImage -dtb vexpress-v2p-ca9-qemuarma9.dtb -append "console=ttyAMA0,115200 console=tty"
```

In this case we have a *kernel panic* because we don't have a rootfs.

## Image

* `qemu-image-devel`. Minimal image for testing and development.
