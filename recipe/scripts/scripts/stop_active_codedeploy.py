# scripts/stop_active_codedeploy.py

import boto3
import os
import sys
import time

def stop_active_codedeploy(application_name, deployment_group_name, region):
    print(f"--- Checking for and stopping any active CodeDeploy deployments for Application: {application_name}, Deployment Group: {deployment_group_name} in region: {region} ---")
    
    try:
        client = boto3.client('codedeploy', region_name=region)
        
        statuses = ['InProgress', 'Pending', 'Queued', 'Created', 'Ready']
        
        # list_deployments 호출 시 applicationName과 deploymentGroupName으로 직접 필터링
        # includeOnlyStatuses는 list_deployments의 직접적인 파라미터가 아니라 list_deployments_by_application 또는 query에서 사용됨
        # 일단 모든 배포 ID를 가져와서 스크립트 내부에서 상태 필터링
        response = client.list_deployments(
            applicationName=application_name,
            deploymentGroupName=deployment_group_name,
        )
        
        all_deployment_ids = response.get('deployments', [])
        
        active_deployment_ids = []
        if all_deployment_ids:
            # 각 배포의 상세 정보를 가져와서 상태를 확인
            for dep_id in all_deployment_ids:
                dep_info = client.get_deployment(deploymentId=dep_id)['deploymentInfo']
                if dep_info['status'] in statuses:
                    active_deployment_ids.append(dep_id)

        if not active_deployment_ids:
            print(f"No active deployments found for {application_name}/{deployment_group_name}.")
            return

        print(f"Found active deployments: {active_deployment_ids}. Attempting to stop them.")
        for dep_id in active_deployment_ids:
            try:
                print(f"Stopping deployment {dep_id}...")
                client.stop_deployment(deploymentId=dep_id)
                print(f"Stopped deployment {dep_id}.")
                time.sleep(5) # 각 중지 명령 후 잠시 대기
            except Exception as e:
                print(f"Failed to stop deployment {dep_id}: {e}", file=sys.stderr)
                # 배포 중지 실패는 치명적이지 않을 수 있으므로, 스크립트 종료 대신 경고만
        print("Active deployments stopped or being stopped. Proceeding with new deployment.")
    
    except Exception as e:
        print(f"An error occurred while checking/stopping CodeDeploy deployments: {e}", file=sys.stderr)
        # Boto3 오류 발생 시 Jenkins 빌드를 실패시켜야 하므로 exit(1)
        sys.exit(1)

if __name__ == "__main__":
    app_name = os.environ.get('CODEDEPLOY_APPLICATION')
    dep_group_name = os.environ.get('CODEDEPLOY_DEPLOYMENT_GROUP')
    aws_region = os.environ.get('AWS_REGION')

    if not all([app_name, dep_group_name, aws_region]):
        print("ERROR: Environment variables CODEDEPLOY_APPLICATION, CODEDEPLOY_DEPLOYMENT_GROUP, and AWS_REGION must be set.", file=sys.stderr)
        sys.exit(1)

    stop_active_codedeploy(app_name, dep_group_name, aws_region)
