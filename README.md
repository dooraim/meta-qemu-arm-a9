# meta-qemu-arm-a9

## Structure of directory and file

```bash
.
├── meta-qemu-arm-a9-app/
│   ├── conf/
│   │   └── layer.conf
│   ├── recipes-core/
│   │   └── images/
│   │       ├── qemu-image-base.inc
│   │       ├── qemu-image-debug.bb
│   │       └── qemu-image-devel.bb
│   └── recipes-helloworld/
│       └── helloworld/
│           ├── files/
│           │   └── helloworld.c
│           └── helloworld_0.1.bb
├── meta-qemu-arm-a9-bsp/
│   ├── conf/
│   │   ├── machine/
│   │   │   └── qemuarma9.conf
│   │   └── layer.conf
│   └── recipes-kernel/
│       └── linux-yocto/
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
```
╰─❯ cat ../build/conf/bblayers.conf
# POKY_BBLAYERS_CONF_VERSION is increased each time build/conf/bblayers.conf
# changes incompatibly
POKY_BBLAYERS_CONF_VERSION = "2"

BBPATH = "${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \
  /home/norlando/yocto/qemu/poky/meta \
  /home/norlando/yocto/qemu/poky/meta-poky \
  /home/norlando/yocto/qemu/poky/meta-yocto-bsp \
  /home/norlando/yocto/qemu/meta-openembedded/meta-initramfs \
  /home/norlando/yocto/qemu/meta-openembedded/meta-filesystems \
  /home/norlando/yocto/qemu/meta-openembedded/meta-gnome \
  /home/norlando/yocto/qemu/meta-openembedded/meta-multimedia \
  /home/norlando/yocto/qemu/meta-openembedded/meta-networking \
  /home/norlando/yocto/qemu/meta-openembedded/meta-oe \
  /home/norlando/yocto/qemu/meta-openembedded/meta-perl \
  /home/norlando/yocto/qemu/meta-openembedded/meta-python \
  /home/norlando/yocto/qemu/meta-openembedded/meta-webserver \
  /home/norlando/yocto/qemu/meta-openembedded/meta-xfce \
  /home/norlando/yocto/qemu/meta-qemu-arm-a9/meta-qemu-arm-a9-app \
  /home/norlando/yocto/qemu/meta-qemu-arm-a9/meta-qemu-arm-a9-bsp \
  /home/norlando/yocto/qemu/meta-qemu-arm-a9/meta-qemu-arm-a9-distro \
  "
```
In the `conf/layer.conf` file, add the machine `qemuarma9`.

## Test

On your VM install the follow packages:

```
$ sudo apt-get update && sudo apt install -y qemu-system-arm 
 libssl-dev device-tree-compiler swig python3-distutils python3-dev \
 bridge-utils net-tools nfs-kernel-server uml-utilities \
 tftpd-hpa \
```

### Testing Kernel

For build kernel execute the follow command:

```bash
$ bitbake virtual/kernel
```

For testing kernel on VM, performd follow command:

```bash
$ qemu-system-arm -M vexpress-a9 -m 128M -nographic -kernel zImage -dtb vexpress-v2p-ca9-qemuarma9.dtb -append "console=ttyAMA0,115200 console=tty"
```

In this case we have a *kernel panic* because we don't have a rootfs.

### Testing rootfs via NFS

Perform follow command:

```bash
$ bitbake qemu-image-davel
```

Into VM perform follow step.

Create the script `qemu-tap-setup.sh`.

<p>
<details>
<summary>qemu-tap-setup.sh</summary>

<pre><code>
#!/bin/bash

# qemu-tap-setup.sh

echo "Create a bridge"
brctl addbr br0

echo "Add enp0s1 to bridge"
brctl addif br0 enp0s1

echo "Create tap interface"
tunctl -t tap0 -u dooraim

echo "Add tap0 to bridge"
brctl addif br0 tap0

echo "Make sure everything is up"
ifconfig enp0s1 up
ifconfig tap0 up
ifconfig br0 up

echo "Check if properly bridged"
brctl show

echo "Assign ip to br0"
dhclient -v br0
</code></pre>

</details>
</p>

Change permission to `qemu-tap-setup.sh`

```bash
chmod u+x qemu-tap-setup.sh
```

To determine the ip address of the `tap0` interface, increase the ip address of the bridge by 1. For example, if the bridge is associated with the ip address 10.0.2.16, then that of tap0 will be 10.0.2.17.

Unpack the rootfs.tar.bz2 file, containing the rootfs, into a folder.

Into `/etc/exports` file add following raw:

```bash
$ /home/<user>/<path rootfs> <ip tap0>(rw,no_root_squash,no_subtree_check)
```

Finally perform the following command:

```bash
$ sudo exportfs -r
```

```bash
$ sudo qemu-system-arm -M vexpress-a9 -m 128M -nographic -kernel <path>/zImage -dtb <path>/vexpress-v2p-ca9-qemuarma9.dtb -append "console=ttyAMA0 root=/dev/nfs ip=<ip target> nfsroot=<ip host>:<path rootfs>,nfsvers=3,tcp rw" -net tap,ifname=tap0,script=no -net nic
```

<p>
<details>
<summary>Bootlog of `qemu-imge-devel`</summary>

<pre><code>
[    0.000000] Booting Linux on physical CPU 0x0
[    0.000000] Linux version 5.4.237-yocto-standard (oe-user@oe-host) (gcc version 9.5.0 (GCC)) #1 SMP PREEMPT Sat Mar 18 03:22:05 UTC 2023
[    0.000000] CPU: ARMv7 Processor [410fc090] revision 0 (ARMv7), cr=10c5387d
[    0.000000] CPU: PIPT / VIPT nonaliasing data cache, VIPT nonaliasing instruction cache
[    0.000000] OF: fdt: Machine model: V2P-CA9
[    0.000000] Memory policy: Data cache writeback
[    0.000000] Reserved memory: created DMA memory pool at 0x4c000000, size 8 MiB
[    0.000000] OF: reserved mem: initialized node vram@4c000000, compatible id shared-dma-pool
[    0.000000] CPU: All CPU(s) started in SVC mode.
[    0.000000] percpu: Embedded 19 pages/cpu s47692 r8192 d21940 u77824
[    0.000000] Built 1 zonelists, mobility grouping on.  Total pages: 32480
[    0.000000] Kernel command line: console=ttyAMA0 root=/dev/nfs ip=10.0.2.17 nfsroot=10.0.2.15:/home/dooraim/rootfs,nfsvers=3,tcp rw
[    0.000000] Dentry cache hash table entries: 16384 (order: 4, 65536 bytes, linear)
[    0.000000] Inode-cache hash table entries: 8192 (order: 3, 32768 bytes, linear)
[    0.000000] mem auto-init: stack:off, heap alloc:off, heap free:off
[    0.000000] Memory: 116088K/131072K available (8192K kernel code, 728K rwdata, 2188K rodata, 1024K init, 287K bss, 14984K reserved, 0K cma-reserved, 0K highmem)
[    0.000000] SLUB: HWalign=64, Order=0-3, MinObjects=0, CPUs=4, Nodes=1
[    0.000000] ftrace: allocating 31405 entries in 93 pages
[    0.000000] rcu: Preemptible hierarchical RCU implementation.
[    0.000000] 	Tasks RCU enabled.
[    0.000000] rcu: RCU calculated value of scheduler-enlistment delay is 10 jiffies.
[    0.000000] NR_IRQS: 16, nr_irqs: 16, preallocated irqs: 16
[    0.000000] GIC CPU mask not found - kernel will fail to boot.
[    0.000000] GIC CPU mask not found - kernel will fail to boot.
[    0.000000] L2C: platform modifies aux control register: 0x02020000 -> 0x02420000
[    0.000000] L2C: DT/platform modifies aux control register: 0x02020000 -> 0x02420000
[    0.000000] L2C-310 enabling early BRESP for Cortex-A9
[    0.000000] L2C-310 full line of zeros enabled for Cortex-A9
[    0.000000] L2C-310 dynamic clock gating disabled, standby mode disabled
[    0.000000] L2C-310 cache controller enabled, 8 ways, 128 kB
[    0.000000] L2C-310: CACHE_ID 0x410000c8, AUX_CTRL 0x46420001
[    0.000251] sched_clock: 32 bits at 24MHz, resolution 41ns, wraps every 89478484971ns
[    0.005063] clocksource: arm,sp804: mask: 0xffffffff max_cycles: 0xffffffff, max_idle_ns: 1911260446275 ns
[    0.005909] smp_twd: clock not found -2
[    0.010657] Console: colour dummy device 80x30
[    0.011095] Calibrating local timer... 93.04MHz.
[    0.066034] Calibrating delay loop... 841.31 BogoMIPS (lpj=4206592)
[    0.139054] pid_max: default: 32768 minimum: 301
[    0.140067] LSM: Security Framework initializing
[    0.141311] Mount-cache hash table entries: 1024 (order: 0, 4096 bytes, linear)
[    0.141362] Mountpoint-cache hash table entries: 1024 (order: 0, 4096 bytes, linear)
[    0.156389] CPU: Testing write buffer coherency: ok
[    0.157449] CPU0: Spectre v2: using BPIALL workaround
[    0.167549] CPU0: thread -1, cpu 0, socket 0, mpidr 80000000
[    0.173561] Setting up static identity map for 0x60100000 - 0x60100060
[    0.174375] rcu: Hierarchical SRCU implementation.
[    0.178429] smp: Bringing up secondary CPUs ...
[    0.185158] smp: Brought up 1 node, 1 CPU
[    0.185239] SMP: Total of 1 processors activated (841.31 BogoMIPS).
[    0.185326] CPU: All CPU(s) started in SVC mode.
[    0.194955] devtmpfs: initialized
[    0.206913] VFP support v0.3: implementor 41 architecture 3 part 30 variant 9 rev 0
[    0.233278] clocksource: jiffies: mask: 0xffffffff max_cycles: 0xffffffff, max_idle_ns: 19112604462750000 ns
[    0.234016] futex hash table entries: 1024 (order: 4, 65536 bytes, linear)
[    0.236545] xor: measuring software checksum speed
[    0.337568]    arm4regs  :   903.600 MB/sec
[    0.429556]    8regs     :  1204.400 MB/sec
[    0.522404]    32regs    :   994.400 MB/sec
[    0.522504] xor: using function: 8regs (1204.400 MB/sec)
[    0.553088] NET: Registered protocol family 16
[    0.557141] DMA: preallocated 256 KiB pool for atomic coherent allocations
[    0.679810] hw-breakpoint: debug architecture 0x4 unsupported.
[    0.679952] Serial: AMBA PL011 UART driver
[    0.690607] 10009000.uart: ttyAMA0 at MMIO 0x10009000 (irq = 29, base_baud = 0) is a PL011 rev1
[    0.698325] printk: console [ttyAMA0] enabled
[    0.701458] 1000a000.uart: ttyAMA1 at MMIO 0x1000a000 (irq = 30, base_baud = 0) is a PL011 rev1
[    0.703552] 1000b000.uart: ttyAMA2 at MMIO 0x1000b000 (irq = 31, base_baud = 0) is a PL011 rev1
[    0.706252] 1000c000.uart: ttyAMA3 at MMIO 0x1000c000 (irq = 32, base_baud = 0) is a PL011 rev1
[    0.708692] OF: amba_device_add() failed (-19) for /smb@4000000/motherboard/iofpga@7,00000000/wdt@f000
[    0.712304] OF: amba_device_add() failed (-19) for /memory-controller@100e0000
[    0.712808] OF: amba_device_add() failed (-19) for /memory-controller@100e1000
[    0.713656] OF: amba_device_add() failed (-19) for /watchdog@100e5000
[    0.715644] irq: type mismatch, failed to map hwirq-75 for interrupt-controller@1e001000!
[    0.913295] raid6: int32x8  gen()    84 MB/s
[    1.072268] raid6: int32x8  xor()    56 MB/s
[    1.238109] raid6: int32x4  gen()    95 MB/s
[    1.405651] raid6: int32x4  xor()    56 MB/s
[    1.563697] raid6: int32x2  gen()   537 MB/s
[    1.722155] raid6: int32x2  xor()   328 MB/s
[    1.890506] raid6: int32x1  gen()   542 MB/s
[    2.047532] raid6: int32x1  xor()   351 MB/s
[    2.047661] raid6: using algorithm int32x1 gen() 542 MB/s
[    2.047736] raid6: .... xor() 351 MB/s, rmw enabled
[    2.047843] raid6: using intx1 recovery algorithm
[    2.052980] SCSI subsystem initialized
[    2.054420] usbcore: registered new interface driver usbfs
[    2.054786] usbcore: registered new interface driver hub
[    2.055360] usbcore: registered new device driver usb
[    2.092948] clocksource: Switched to clocksource arm,sp804
[    2.638537] NET: Registered protocol family 2
[    2.639761] IP idents hash table entries: 2048 (order: 2, 16384 bytes, linear)
[    2.649451] tcp_listen_portaddr_hash hash table entries: 512 (order: 0, 6144 bytes, linear)
[    2.649885] TCP established hash table entries: 1024 (order: 0, 4096 bytes, linear)
[    2.650128] TCP bind hash table entries: 1024 (order: 1, 8192 bytes, linear)
[    2.650340] TCP: Hash tables configured (established 1024 bind 1024)
[    2.653039] UDP hash table entries: 256 (order: 1, 8192 bytes, linear)
[    2.653368] UDP-Lite hash table entries: 256 (order: 1, 8192 bytes, linear)
[    2.658694] NET: Registered protocol family 1
[    2.661868] RPC: Registered named UNIX socket transport module.
[    2.662194] RPC: Registered udp transport module.
[    2.662328] RPC: Registered tcp transport module.
[    2.663530] RPC: Registered tcp NFSv4.1 backchannel transport module.
[    2.674442] hw perfevents: enabled with armv7_cortex_a9 PMU driver, 5 counters available
[    2.678637] workingset: timestamp_bits=14 max_order=15 bucket_order=1
[    2.701799] NFS: Registering the id_resolver key type
[    2.703241] Key type id_resolver registered
[    2.703358] Key type id_legacy registered
[    2.706603] Key type cifs.idmap registered
[    2.741832] Block layer SCSI generic (bsg) driver version 0.4 loaded (major 251)
[    2.742220] io scheduler mq-deadline registered
[    2.742362] io scheduler kyber registered
[    2.747619] clcd-pl11x 1001f000.clcd: PL111 designer 41 rev2 at 0x1001f000
[    2.748777] clcd-pl11x: probe of 1001f000.clcd failed with error -2
[    2.749123] clcd-pl11x 10020000.clcd: PL111 designer 41 rev2 at 0x10020000
[    2.749351] clcd-pl11x: probe of 10020000.clcd failed with error -2
[    2.790854] brd: module loaded
[    2.864131] smsc911x 4e000000.ethernet eth0: MAC Address: 52:54:00:12:34:56
[    2.864956] usbcore: registered new interface driver usb-storage
[    2.875429] rtc-pl031 10017000.rtc: registered as rtc0
[    2.878723] device-mapper: ioctl: 4.41.0-ioctl (2019-09-16) initialised: dm-devel@redhat.com
[    2.883876] mmci-pl18x 10005000.mmci: Got CD GPIO
[    2.884316] mmci-pl18x 10005000.mmci: Got WP GPIO
[    2.886568] mmci-pl18x 10005000.mmci: mmc0: PL181 manf 41 rev0 at 0x10005000 irq 25,26 (pio)
[    2.920462] usbcore: registered new interface driver usbhid
[    2.920592] usbhid: USB HID core driver
[    2.921028] u32 classifier
[    2.921090]     input device check on
[    2.921141]     Actions configured
[    2.923474] NET: Registered protocol family 10
[    2.933295] Segment Routing with IPv6
[    2.934342] sit: IPv6, IPv4 and MPLS over IPv4 tunneling driver
[    2.938061] NET: Registered protocol family 17
[    2.939071] Key type dns_resolver registered
[    2.939412] Registering SWP/SWPB emulation handler
[    2.941559] Key type ._fscrypt registered
[    2.941684] Key type .fscrypt registered
[    2.948258] Btrfs loaded, crc32c=crc32c-generic
[    2.956391] Key type encrypted registered
[    2.960008] printk: console [netcon0] enabled
[    2.960146] netconsole: network logging started
[    2.961198] rtc-pl031 10017000.rtc: setting system clock to 2023-04-22T17:13:01 UTC (1682183581)
[    2.976580] input: AT Raw Set 2 keyboard as /devices/platform/smb@4000000/smb@4000000:motherboard/smb@4000000:motherboard:iofpga@7,00000000/10006000.kmi/serio0/input/input0
[    2.990310] Generic PHY 4e000000.ethernet-ffffffff:01: attached PHY driver [Generic PHY] (mii_bus:phy_addr=4e000000.ethernet-ffffffff:01, irq=POLL)
[    2.991491] smsc911x 4e000000.ethernet eth0: SMSC911x/921x identified at 0xc8930000, IRQ: 22
[    2.994931] IPv6: ADDRCONF(NETDEV_CHANGE): eth0: link becomes ready
[    3.028474] IP-Config: Guessing netmask 255.0.0.0
[    3.028611] IP-Config: Complete:
[    3.028871]      device=eth0, hwaddr=52:54:00:12:34:56, ipaddr=10.0.2.17, mask=255.0.0.0, gw=255.255.255.255
[    3.029036]      host=10.0.2.17, domain=, nis-domain=(none)
[    3.029146]      bootserver=255.255.255.255, rootserver=10.0.2.15, rootpath=
[    3.617528] input: ImExPS/2 Generic Explorer Mouse as /devices/platform/smb@4000000/smb@4000000:motherboard/smb@4000000:motherboard:iofpga@7,00000000/10007000.kmi/serio1/input/input2
[    3.618821] md: Waiting for all devices to be available before autodetect
[    3.618911] md: If you don't use raid, use raid=noautodetect
[    3.623787] md: Autodetecting RAID arrays.
[    3.623916] md: autorun ...
[    3.623987] md: ... autorun DONE.
[    3.719474] VFS: Mounted root (nfs filesystem) on device 0:16.
[    3.723372] devtmpfs: mounted
[    3.767104] Freeing unused kernel memory: 1024K
[    3.769716] Run /sbin/init as init process

INIT: version 2.96 booting

Starting udev
[    6.613346] udevd[118]: starting version 3.2.9
[    6.663617] random: udevd: uninitialized urandom read (16 bytes read)
[    6.679669] random: udevd: uninitialized urandom read (16 bytes read)
[    6.685518] random: udevd: uninitialized urandom read (16 bytes read)
[    6.881470] udevd[119]: starting eudev-3.2.9
[   12.900398] random: dd: uninitialized urandom read (512 bytes read)
[   15.550410] random: dbus-daemon: uninitialized urandom read (12 bytes read)
[   15.658512] random: dbus-daemon: uninitialized urandom read (12 bytes read)
[   18.097141] random: avahi-daemon: uninitialized urandom read (4 bytes read)
[   18.155810] random: avahi-daemon: uninitialized urandom read (4 bytes read)


Poky (Yocto Project Reference Distro) 3.1.24 qemuarma9 /dev/ttyAMA0



qemuarma9 login: root
root@qemuarma9:~#
root@qemuarma9:~# uname -a
Linux qemuarma9 5.4.237-yocto-standard #1 SMP PREEMPT Sat Mar 18 03:22:05 UTC 2023 armv7l GNU/Linux
root@qemuarma9:~# QEMU: Terminated
</code></pre>

</details>
</p>

### Install SDK

Performe the command:

```bash
$ bitbake qemu-image-debug -c populate-sdk`.
```

Copy `poky-glibc-x86_64-qemu-image-debug-armv7at2-vfp-qemuarma9-toolchain-3.1.24.sh` in VM and execute.

```bash
$ ./poky-glibc-x86_64-qemu-image-debug-armv7at2-vfp-qemuarma9-toolchain-3.1.24.sh                                                                     58%
Poky (Yocto Project Reference Distro) SDK installer version 3.1.24
==================================================================
Enter target directory for SDK (default: /opt/poky/3.1.24): 
You are about to install the SDK to "/opt/poky/3.1.24". Proceed [Y/n]? y
Extracting SDK.............................................................done
Setting it up...done
SDK has been successfully set up and is ready to be used.
Each time you wish to use the SDK in a new shell session, you need to source the environment setup script e.g.
 $ . /opt/poky/3.1.24/environment-setup-armv7at2-vfp-poky-linux-gnueabi
```

Example of use toolchain
```bash
$ . /opt/poky/3.1.24/environment-setup-armv7at2-vfp-poky-linux-gnueabi
$ arm-poky-linux-gnueabi-gcc  -march=armv7-a -mthumb -mfpu=vfp -mfloat-abi=softfp -fstack-protector-strong  -D_FORTIFY_SOURCE=2 -Wformat -Wformat-security -Werror=format-security --sysroot=/opt/poky/3.1.24/sysroots/armv7at2-vfp-poky-linux-gnueabi hello.c -o hello
```
On target

```bash
root@qemuarma9:~# ./hello 
Hello world from new toolchain!
```

## Image

* `qemu-image-devel`. Minimal image for testing and development.
* `qemu-image-debug`. Image with GDB.
