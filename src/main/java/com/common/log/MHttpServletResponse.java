package com.common.log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class MHttpServletResponse extends HttpServletResponseWrapper{
	MServletOutputStream teeServletOutputStream;
	PrintWriter teeWriter;
	public MHttpServletResponse(HttpServletResponse response) {
		super(response);
	}
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (teeServletOutputStream == null) {
			teeServletOutputStream = new MServletOutputStream(this.getResponse());
		}
		return teeServletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (this.teeWriter == null) {
			this.teeWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), this.getResponse().getCharacterEncoding()), true);
		}
		return this.teeWriter;
	}

	@Override
	public void flushBuffer() {
		if (this.teeWriter != null) {
			this.teeWriter.flush();
		}
	}

	public byte[] getOutputBuffer() {
		// teeServletOutputStream can be null if the getOutputStream method is never
		// called.
		if (teeServletOutputStream != null) {
			return teeServletOutputStream.getOutputStreamAsByteArray();
		} else {
			return null;
		}
	}

	public void finish() throws IOException {
		if (this.teeWriter != null) {
			this.teeWriter.close();
		}
		if (this.teeServletOutputStream != null) {
			this.teeServletOutputStream.close();
		}
	}
}
