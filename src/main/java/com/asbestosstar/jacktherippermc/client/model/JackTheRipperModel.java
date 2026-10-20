package com.asbestosstar.jacktherippermc.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;


public class JackTheRipperModel extends HumanoidModel<HumanoidRenderState> {

	public JackTheRipperModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(HumanoidRenderState state) {
		super.setupAnim(state);
		this.head.xRot = -this.head.xRot;// My new blockbench like software still has some ironing out to do
		this.head.yRot = -this.head.yRot;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -2.0F, 8.0F, 10.0F,
				4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 10.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 15).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F,
						CubeDeformation.NONE),
				PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, (float) Math.PI));

		head.addOrReplaceChild("hat",
				CubeListBuilder.create().texOffs(0, 28)
						.addBox(-2.0F, -0.472793F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE).texOffs(17, 28)
						.addBox(-4.092432F, 0.036754F, -3.736189F, 8.0F, 0.5F, 8.0F, CubeDeformation.NONE),
				PartPose.ZERO);

		root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(51, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F,
				8.5F, 3.0F, CubeDeformation.NONE), PartPose.offset(-5.302893F, 5.201013F, 0.254569F));

		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(25, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F,
				8.5F, 3.0F, CubeDeformation.NONE), PartPose.offset(4.942846F, 5.201013F, 0.254569F));

		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(38, 15).addBox(-1.5F, 0.0F, -1.5F, 3.0F,
				9.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 15.0F, 0.0F));

		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(25, 15).addBox(-1.5F, 0.0F, -1.5F, 3.0F,
				9.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 15.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}
}
