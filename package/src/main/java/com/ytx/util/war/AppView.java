package com.ytx.util.war;

import com.ytx.util.resource.ClassPathResource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppView extends ApplicationWindow {
    private static volatile boolean taskRunning = false;

    private Text console;
    private AppController controller = new AppController();
    private Executor executor = Executors.newFixedThreadPool(4);

    public AppView() {
        super(null);
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setSize(1550, 800);
        shell.setText("云天下-本地打war包工具");
    }

    protected Control createContents(Composite parent) {
        //创建composite
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(4, false);
        composite.setLayout(gridLayout);
        //创建消息输出框
        createConsule(composite);
        //创建确认按钮
        createBuildButton(composite);
        //创建取消按钮
        createCancelButton(composite);
        //创建生成配置模板按钮
        createTemplateButton(composite);
        //创建生成配置模板按钮
        createBatchModifyDomainButton(composite);
        return parent;
    }

    private void createBatchModifyDomainButton(Composite composite){
        Button buidButton = new Button(composite, SWT.NONE);
        buidButton.setText("批量修改domain");
        buidButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                try {
                    File destDir = getDestDir("选择本地domain文件夹夹");
                    Collection<File> domainList = FileUtils.listFiles(destDir, new String[]{"java"}, false);
                    batchModifyDomain(domainList);
                    MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "消息对话框", "修改完成，请检查修改是否正确，如果正确，请提交到远端!");
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    public void batchModifyDomain(Collection<File> domainFileList) throws IOException{
        for(File domainJavaFile : domainFileList){
            if(checkDomain(domainJavaFile)){
                continue;
            }
            //有问题的代码，自动修改
            String domainCode = FileUtils.readFileToString(domainJavaFile);
            StringBuilder sb = new StringBuilder();
            int packageIndex = domainCode.indexOf("package");
            if(packageIndex == -1){
                break;
            }
            int packagePrefixIndex = domainCode.indexOf(";",packageIndex);
            if(packagePrefixIndex == -1){
                break;
            }
            //将pacakge语句添加到结果中
            sb.append(domainCode.substring(0,packagePrefixIndex + 1)).append("\r\n");
            //如果没有jsonignore的import语句，添加进去
            boolean containsIgnoreImport = domainCode.indexOf("com.fasterxml.jackson.annotation.JsonIgnoreProperties") != -1;
            if(!containsIgnoreImport){
                sb.append("\r\n").append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;").append("\r\n");
            }
            //如果没有Serializable的import语句，添加进去
            boolean containsSerializableImport = domainCode.indexOf("java.io.Serializable") != -1;
            if(!containsSerializableImport){
                sb.append("\r\n").append("import java.io.Serializable;").append("\r\n");
            }
            //找到最后一个import以及结尾的分号，将剩余的import添加进去
            int lastImportIndex = domainCode.lastIndexOf("import");
            int lastImportPrefixIndex = 0;
            if(lastImportIndex != -1){
                lastImportPrefixIndex = domainCode.indexOf(";",lastImportIndex);
                if(lastImportPrefixIndex == -1){
                    break;
                }
                sb.append(domainCode.substring(packagePrefixIndex + 1,lastImportPrefixIndex + 1));
            }else{
                lastImportPrefixIndex = packagePrefixIndex;
            }
            //检查有没有@JsonIgnoreProperties(value = {"handler"})
            int publicIndex = domainCode.indexOf("public");
            if(publicIndex == -1){
                break;
            }
            //将import之后，public class之前的部分添加到结果中去
            sb.append(domainCode.substring(lastImportPrefixIndex + 1,publicIndex));
            if(domainCode.indexOf("@JsonIgnoreProperties(value = {\"handler\"})") == -1){
                sb.append("@JsonIgnoreProperties(value = {\"handler\"})").append("\r\n");
            }
            int braceStartIndex = domainCode.indexOf("{",publicIndex);
            if(braceStartIndex == -1){
                break;
            }
            //将public之后，类开始的{之前的部分添加结果中
            sb.append(domainCode.substring(publicIndex,braceStartIndex));
            if(domainCode.indexOf("Serializable",publicIndex) == -1){
                //没有实现Serializable接口
                sb.append(" implements Serializable ");
            }
            //将类开始的花括号添加到结果中
            sb.append(domainCode.substring(braceStartIndex,braceStartIndex + 1));
            if(domainCode.indexOf("serialVersionUID") == -1){
                sb.append("\r\n").append("    private static final long serialVersionUID = 1L;").append("\r\n");
            }
            sb.append(domainCode.substring(braceStartIndex + 1));
            String resultDomainCode = sb.toString();
            FileUtils.write(domainJavaFile,resultDomainCode);
        }
    }

    private boolean checkDomain(File domainJavaFile) throws IOException{
        StringBuilder errorMsg = new StringBuilder();
        AppController.checkSingleDomain(domainJavaFile,errorMsg);
        if(errorMsg.toString().isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    private void createTemplateButton(Composite composite){
        Button cancelButton = new Button(composite, SWT.NONE);
        cancelButton.setText("模板");
        cancelButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                try {
                    File destDir = getDestDir("模板保存目标文件夹选择");
                    copyTemplateFile("learning_service(git).properties", destDir);
                    copyTemplateFile("statistic_web(svn).properties", destDir);
                    MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "消息对话框", "模板保存成功!");
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    private void copyTemplateFile(String sampleFileName,File destDir) throws IOException{
        ClassPathResource resource = ClassPathResource.getInstance("config/" + sampleFileName);
        List<String> lines = IOUtils.readLines(resource.getInputStream());
        File destFile = new File(destDir,sampleFileName);
        FileUtils.writeLines(destFile,lines);
    }


    private File getDestDir(String text){
        DirectoryDialog folderDlg = new DirectoryDialog(getMyShell());
        folderDlg.setText("模板保存目标文件夹选择");
        String selectedDir = folderDlg.open();
        File destDir = new File(selectedDir);
        return destDir;
    }

    private void createCancelButton(Composite composite){
        Button cancelButton = new Button(composite, SWT.NONE);
        cancelButton.setText("取消");
        cancelButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean b = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
                        "确认取消",
                        "确实取消吗？");
                if(true) {
                    Display.getCurrent().dispose();
                    System.exit(0);
                }
            }
        });
    }

    Shell getMyShell(){
        return super.getShell();
    }

    private void createBuildButton(Composite composite){
        Button buidButton = new Button(composite, SWT.NONE);
        buidButton.setText("打包");
        buidButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(taskRunning){
                    MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "消息对话框", "请等待当前打包结束,才能发起新的打包");
                    return;
                }
                taskRunning = true;
                buidButton.setGrayed(true);
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        console.setText("");
                    }
                });
                boolean b = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
                        "确认修改全部push",
                        "所有修改已经push到远端吗？");
                if(b == false){
                    MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "消息对话框", "请先push到远端");
                    return;
                }
                try{
                    FileDialog dialog = new FileDialog(getMyShell(),SWT.OPEN|SWT.MULTI);
                    dialog.setFilterPath("");// 设置默认的路径
                    dialog.setText("选择配置文件");//设置对话框的标题
                    dialog.setFileName("");//设置默认的文件名
                    dialog.setFilterNames(new String[] { "文本文件 (*.properties)", "所有文件(*.*)" });//设置扩展名
                    dialog.setFilterExtensions(new String[] { "*.properties", "*.*" });//设置文件扩展名
                    String fileName = dialog.open();//返回最后一个选择文件的全路径
                    String[] fileNames = dialog.getFileNames();//返回所有选择的文件名，不包括路径
                    String path = dialog.getFilterPath();//返回选择的路径，这个和fileNames配合可以得到所有的文件的全路径
                    List<File> fileList = convertToFileList(path,fileNames);
                    executor.execute(new Runnable(){
                        @Override
                        public void run(){
                            try {
                                controller.run(fileList);
                            }catch(Exception ex){
                                Display.getDefault().syncExec(new Runnable() {
                                    public void run() {
                                        console.append("\n" + ex.getMessage());
                                    }
                                });
                            }finally{
                                taskRunning = false;
                            }
                        }
                    });
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            while(true){
                                try {
                                    String log = AppController.takeLog();
                                    if (log.trim().equals("exit")) {
                                        break;
                                    }
                                    Display.getDefault().syncExec(new Runnable() {
                                        public void run() {
                                            console.append(log);
                                        }
                                    });
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }catch(Exception ex){
                    console.append("\n" + ex.getMessage());
                }
            }
        });
    }

    private List<File> convertToFileList(String path, String[] fileNames){
        List<File> fileList = new ArrayList();
        File dir = new File(path);
        for(String fileName : fileNames){
            File f = new File(dir,fileName);
            fileList.add(f);
        }
        return fileList;
    }

    private void createConsule(Composite composite){
        final Text console = new Text(composite, SWT.NONE | SWT.READ_ONLY | SWT.V_SCROLL);
        this.console = console;
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 6;
        console.setLayoutData(data);
    }

    public static void main(String[] args) {
        AppView test = new AppView();
        test.setBlockOnOpen(true);
        test.open();
        Display.getCurrent().dispose();
        System.exit(0);
    }


}

