package com.p2p.p4f.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerP4F {
	private static Logger logger = LogManager.getLogger(ServerP4F.class.getName());
	private Channel serverChannel = null;
	private int port;
	private String ipAddr = null;
	
	public ServerP4F(String ip, int port) {
		if (!ip.isEmpty())
			ipAddr = ip;
		this.port = port;
	}
	
	public void start() throws Exception {
		//Run the server on another thread
		Thread serverThread = new Thread(() -> {
			EventLoopGroup bossGroup = new NioEventLoopGroup();
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			try {
				//Setup the server
				ServerBootstrap serverBs = new ServerBootstrap();
				serverBs.group(bossGroup, workerGroup)
						.channel(NioServerSocketChannel.class)
						.childHandler(new ChannelInitializer<NioSocketChannel>() {
							@Override
							protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
								//TODO: Add server handlers here
								
							}
						})
						.option(ChannelOption.SO_BACKLOG, 128)
						.childOption(ChannelOption.SO_KEEPALIVE, true)
						.childOption(ChannelOption.SO_RCVBUF, 8 * 1024)
						.childOption(ChannelOption.SO_SNDBUF, 8 * 1024);
				//Bind the server to an address + port
				ChannelFuture chFuture = serverBs.bind(ipAddr, port).sync();
				logger.info("P4F Server starts successfully on port " + port + (ipAddr == null ? " of all network interfaces" : " of address " + ipAddr));
				//Keep the server channel for manual closing
				serverChannel = chFuture.channel();
				//Block this thread until all groups finish
				serverChannel.closeFuture().sync();
			} catch (Exception e) {
				logger.error("P4F Server - Netty error", e);
			} finally {
				//Close the event loop groups and remove the current server channel
				workerGroup.shutdownGracefully();
				bossGroup.shutdownGracefully();
				serverChannel = null;
			}
		});
		
		//Start the server thread
		serverThread.setName("ServerP4F-Thread-" + (ipAddr == null ? ":" : ipAddr + ":") + port);
		serverThread.setDaemon(true);
		serverThread.start();
	}
	
	public void closeServer() {
		if (serverChannel != null) {
			logger.info("Close ServerP4F on " + (ipAddr == null ? ":" : ipAddr + ":") + port);
			serverChannel.close();
			serverChannel = null;
		}
	}
}
